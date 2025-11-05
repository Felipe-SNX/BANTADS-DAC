const { Router } = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');

const { verifyToken, checkRole } = require('../../middlewares/auth.middleware');
const router = Router();
const axios = require('axios');

const orquestradorServiceProxy = createProxyMiddleware({
    target: process.env.MS_ORQUESTRADOR_URL,
    changeOrigin: true,
    logLevel: 'debug',
});

const clientesServiceProxy = createProxyMiddleware({
    target: process.env.MS_CLIENTE_URL,
    changeOrigin: true,
    logLevel: 'debug',
    pathRewrite: (path, req) => {
        let finalPath = path;
        if (path === '/' || path.startsWith('/?')) {
            const [basePath, queryString] = path.split('?'); // basePath será "/"
            finalPath = '/clientes' + (queryString ? '?' + queryString : '');
        }
        console.log(`[Proxy MS-Cliente] Original: "${req.originalUrl}", Reescrito para: "${finalPath}"`);
        return finalPath;
    },
});

const gerentesServiceProxy = createProxyMiddleware({
    target: process.env.MS_GERENTE_URL,
    changeOrigin: true,
    logLevel: 'debug',
    pathRewrite: (path, req) => {
        const finalPath = '/gerentes' + (path === '/' ? '' : path);
        console.log(`[Proxy MS-Gerente] Original: "${req.originalUrl}", Reescrito para: "${finalPath}"`);
        return finalPath;
    },
});

const contasServiceProxy = createProxyMiddleware({
    target: process.env.MS_CONTA_URL,
    changeOrigin: true,
    logLevel: 'debug',
    pathRewrite: (path, req) => {
        const finalPath = '/contas' + (path === '/' ? '' : path);
        console.log(`[Proxy MS-Conta] Original: "${req.originalUrl}", Reescrito para: "${finalPath}"`);
        return finalPath;
    },
});

const authServiceProxy = createProxyMiddleware({
    target: process.env.MS_AUTH_URL,
    changeOrigin: true,
    logLevel: 'debug',
});

router.get('/reboot', (req, res, next) => {
    return res.status(200).json({ message: 'Banco de dados criado conforme especificação' });
}) 

router.post('/login', (req, res, next) => {
    req.url = '/auth/login';
    authServiceProxy(req, res, next);
});

router.post('/logout', verifyToken, (req, res, next) => {
    req.url = '/auth/logout';
    authServiceProxy(req, res, next);
});

router.post('/clientes', (req, res, next) => {
    clientesServiceProxy(req, res, next);
});

router.put('/clientes/:cpf', verifyToken, (req, res, next) => {
    clientesServiceProxy(req, res, next);
});

router.post('/gerentes', verifyToken, checkRole(['ADMIN']), (req, res, next) => {
    gerentesServiceProxy(req, res, next);
});

router.put('/gerentes/:cpf', verifyToken, checkRole(['ADMIN']), (req, res, next) => {
    gerentesServiceProxy(req, res, next);
});

router.delete('/gerentes/:cpf', verifyToken, checkRole(['ADMIN']), (req, res, next) => {
    gerentesServiceProxy(req, res, next);
});

router.post('/clientes/:cpf/rejeitar', verifyToken, checkRole(['GERENTE']), clientesServiceProxy);
router.post('/clientes/:cpf/aprovar', verifyToken, checkRole(['GERENTE']), clientesServiceProxy);

router.get('/clientes/:cpf', verifyToken, async (req, res, next) => {
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
            console.warn(`[Gateway] MS-Conta falhou para CPF ${cpf} (esperado, saga pendente?): ${error.message}`);
            return null;
        });

        const [clienteResponse, contaResponse] = await Promise.all([
            clienteRequest,
            contaRequest
        ]);

        const clienteData = clienteResponse.data;

        const cpfGerente = (clienteData && clienteData.cpfGerente) ? clienteData.cpfGerente : null;
        console.log(cpfGerente);
        let gerenteRequest;
        if (cpfGerente) {
            const gerenteUrl = `${process.env.MS_GERENTE_URL}/gerentes/${cpfGerente}`;
            gerenteRequest = axios.get(gerenteUrl, {
                headers: { 'Authorization': authorization }
            }).catch(error => {
                console.warn(`[Gateway] MS-Gerente falhou para CPF ${cpfGerente}: ${error.message}`);
                return null;
            });
        } else {
            gerenteRequest = Promise.resolve(null);
        }

        const gerenteResponse = await gerenteRequest;

        const contaData = (contaResponse && contaResponse.data) ? contaResponse.data : null;
        const gerenteData = (gerenteResponse && gerenteResponse.data) ? gerenteResponse.data : null;

        const limite = contaData ? contaData.limite : null;
        const saldo = contaData ? contaData.saldo : null;
        const conta = contaData ? contaData.numConta : null; // Mantive seu 'numConta'

        const gerente = gerenteData ? gerenteData.cpf : null;
        const gerente_email = gerenteData ? gerenteData.email : null;
        const gerente_nome = gerenteData ? gerenteData.nome : null;

        const compositeResponse = {
            ...clienteData,
            limite: limite,
            conta: conta,
            saldo: saldo,
            gerente: gerente,
            gerente_nome: gerente_nome,
            gerente_email: gerente_email
        };

        res.status(200).json(compositeResponse);

    } catch (error) {
        console.error(`[Gateway] Erro CRÍTICO ao buscar dados primários do cliente ${cpf}:`, error.message);

        const status = error.response?.status || 500;
        res.status(status).json({
            message: 'Erro ao buscar dados primários do cliente.',
            serviceError: error.message
        });
    }
});

router.use('/gerentes', verifyToken, gerentesServiceProxy);
router.use('/clientes', verifyToken, clientesServiceProxy);
router.use('/contas', verifyToken, contasServiceProxy);

module.exports = router;