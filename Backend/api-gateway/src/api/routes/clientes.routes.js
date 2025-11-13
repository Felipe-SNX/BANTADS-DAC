const { Router } = require('express');
const axios = require('axios');
const { verifyToken, checkRole } = require('../../middlewares/auth.middleware');
const { orquestradorServiceProxy, clientesServiceProxy } = require('../../proxies');

const router = Router();

router.post('/', async (req, res, next) => {
    try {
        const { cpf } = req.body;
        if (!cpf) {
            return res.status(400).json({ message: "CPF é obrigatório." });
        }

        const clienteUrl = `${process.env.MS_CLIENTE_URL}/clientes/checkCpf/${cpf}`;
        await axios.get(clienteUrl);

        console.log("indo para a saga");
        req.url = '/saga/autocadastro';
        orquestradorServiceProxy(req, res, next);

    } catch (error) {
        if (error.response && error.response.status === 409) {
            return res.status(409).json(error.response.data);
        }
        next(error);
    }
});

router.get('/:cpf', verifyToken, async (req, res, next) => {
    const { cpf } = req.params;
    const { authorization } = req.headers;

    const clienteUrl = `${process.env.MS_CLIENTE_URL}/clientes/${cpf}`;
    const contaUrl = `${process.env.MS_CONTA_URL}/contas/${cpf}/dadosConta`;

    try {
        const clienteRequest = axios.get(clienteUrl, {
            headers: { 'Authorization': authorization }
        });

        const contaRequest = axios.get(contaUrl, {
            headers: { 'Authorization': authorization }
        }).catch(error => {
            console.warn(`MS-Conta falhou para CPF ${cpf}: ${error.message}`);
            return null;
        });

        const [clienteResponse, contaResponse] = await Promise.all([
            clienteRequest,
            contaRequest
        ]);

        const clienteData = clienteResponse.data;
        const cpfGerente = (clienteData && clienteData.gerente) ? clienteData.gerente : null;
        let gerenteRequest;

        if (cpfGerente) {
            const gerenteUrl = `${process.env.MS_GERENTE_URL}/gerentes/${cpfGerente}`;
            gerenteRequest = axios.get(gerenteUrl, {
                headers: { 'Authorization': authorization }
            }).catch(error => {
                console.warn(`MS-Gerente falhou para CPF ${cpfGerente}: ${error.message}`);
                return null;
            });
        } else {
            gerenteRequest = Promise.resolve(null);
        }

        const gerenteResponse = await gerenteRequest;

        const contaData = (contaResponse?.data) ? contaResponse.data : null;
        const gerenteData = (gerenteResponse?.data) ? gerenteResponse.data : null;

        const compositeResponse = {
            ...clienteData,
            limite: contaData ? contaData.limite : null,
            conta: contaData ? contaData.conta : null,
            saldo: contaData ? contaData.saldo : null,
            gerente: gerenteData ? gerenteData.cpf : null,
            gerente_nome: gerenteData ? gerenteData.nome : null,
            gerente_email: gerenteData ? gerenteData.email : null
        };

        res.status(200).json(compositeResponse);

    } catch (error) {
        console.error(`Erro ao buscar dados primários do cliente ${cpf}:`, error.message);
        const status = error.response?.status || 500;
        res.status(status).json({
            message: 'Erro ao buscar dados primários do cliente.',
            serviceError: error.message
        });
    }
});

router.put('/:cpf', verifyToken, (req, res, next) => {
    req.url = `/saga/alterarPerfil/${req.params.cpf}`;
    orquestradorServiceProxy(req, res, next);
});

router.post('/:cpf/rejeitar', verifyToken, checkRole(['GERENTE']), clientesServiceProxy);

router.post('/:cpf/aprovar', verifyToken, checkRole(['GERENTE']), (req, res, next) => {
    req.url = `/saga/${req.params.cpf}/aprovar`;
    orquestradorServiceProxy(req, res, next);
});

router.use('/', verifyToken, clientesServiceProxy);

module.exports = router;