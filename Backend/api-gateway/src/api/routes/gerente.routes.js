// Arquivo: /src/routes/gerente.routes.js
const { Router } = require('express');
const axios = require('axios');
const { checkRole, verifyToken } = require('../../middlewares/auth.middleware');
const { orquestradorServiceProxy, gerentesServiceProxy } = require('../../proxies');

const router = Router();


router.post('/', verifyToken, checkRole(['ADMINISTRADOR']), async (req, res, next) => {
    try {
        const { cpf } = req.body;

        if (!cpf) {
            return res.status(400).json({ message: "CPF é obrigatório." });
        }

        const clienteUrl = `${process.env.MS_GERENTE_URL}/gerentes/checkCpf/${cpf}`;
        await axios.get(clienteUrl);

        console.log("indo para a saga");
        req.url = '/saga/inserirGerente';
        orquestradorServiceProxy(req, res, next);

    } catch (error) {
        if (error.response && error.response.status === 409) {
            return res.status(409).json(error.response.data);
        }
        next(error);
    }
});


router.delete('/:cpf', verifyToken, checkRole(['ADMINISTRADOR']), (req, res, next) => {
    req.url = `/saga/removerGerente/${req.params.cpf}`;
    orquestradorServiceProxy(req, res, next);
});


router.put('/:cpf', verifyToken, checkRole(['ADMINISTRADOR']), gerentesServiceProxy);


router.get('/', verifyToken, async (req, res, next) => {
    const { filtro } = req.query;

    try {
        const gerenteUrl = `${process.env.MS_GERENTE_URL}/gerentes`;
        const gerenteResponse = await axios.get(gerenteUrl);
        const gerenteData = gerenteResponse.data;

        if (filtro === 'dashboard') {
            const contaUrl = `${process.env.MS_CONTA_URL}/contas/dadosConta`;
            const contaResponse = await axios.get(contaUrl);
            const contaData = contaResponse.data;

            const dashboardMap = new Map();

            for (const gerente of gerenteData) {
                dashboardMap.set(gerente.cpf, {
                    gerente: gerente,
                    clientes: [],
                    saldo_positivo: 0,
                    saldo_negativo: 0
                });
            }

            for (const conta of contaData) {
                const cpfGerente = conta.gerente;
                const dashboardEntry = dashboardMap.get(cpfGerente);

                if (dashboardEntry) {
                    dashboardEntry.clientes.push({
                        cliente: conta.cliente,
                        numero: conta.numero,
                        saldo: conta.saldo,
                        limite: conta.limite,
                        gerente: conta.gerente,
                        criacao: conta.criacao
                    });

                    if (conta.saldo > 0) {
                        dashboardEntry.saldo_positivo += conta.saldo;
                    } else {
                        dashboardEntry.saldo_negativo += conta.saldo;
                    }
                }
            }
            const compositeResponse = Array.from(dashboardMap.values());
            return res.status(200).json(compositeResponse);
        }

        return res.status(200).json(gerenteData);

    } catch (error) {
        console.error(`Erro ao buscar dados de gerentes:`, error.message);
        const status = error.response?.status || 500;
        res.status(status).json({
            message: 'Erro ao buscar dados de gerentes.',
            serviceError: error.message
        });
    }
});

router.use('/', gerentesServiceProxy);

module.exports = router;