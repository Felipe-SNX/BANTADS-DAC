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
    req.url = `/saga/alterarPerfil/${req.params.cpf}`;
    orquestradorServiceProxy(req, res, next);
});

router.post('/gerentes', verifyToken, checkRole(['ADMIN']), (req, res, next) => {
    req.url = '/saga/inserirGerente';
    orquestradorServiceProxy(req, res, next);
});

router.put('/gerentes/:cpf', verifyToken, checkRole(['ADMIN']), (req, res, next) => {
    gerentesServiceProxy(req, res, next);
});

router.delete('/gerentes/:cpf', verifyToken, checkRole(['ADMIN']), (req, res, next) => {
    req.url = `/saga/removerGerente/${req.params.cpf}`;
    orquestradorServiceProxy(req, res, next);
});

router.post('/clientes/:cpf/rejeitar', verifyToken, clientesServiceProxy);
router.post('/clientes/:cpf/aprovar', verifyToken, clientesServiceProxy);

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
        });

        const [clienteResponse, contaResponse] = await Promise.all([
            clienteRequest,
            contaRequest
        ]);

        const clienteData = clienteResponse.data; 
        const contaData = contaResponse.data;    

        const compositeResponse = {
            ...clienteData,
            limite: contaData.limite 
        };

        res.status(200).json(compositeResponse);

    } catch (error) {
        console.error(`Erro ao buscar dados para CPF ${cpf}:`, error.message);
        
        res.status(500).json({ 
            message: 'Erro ao compor a resposta dos microsserviços.',
            serviceError: error.message
        });
    }
});

router.use('/gerentes', verifyToken, gerentesServiceProxy);
router.use('/clientes', verifyToken, clientesServiceProxy);
router.use('/contas', verifyToken, contasServiceProxy);

module.exports = router;