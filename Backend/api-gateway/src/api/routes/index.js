const { Router } = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');

const router = Router();

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
        const finalPath = '/clientes' + (path === '/' ? '' : path);
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

router.post('/clientes', (req, res, next) => {
    req.url = '/saga/autocadastro';
    orquestradorServiceProxy(req, res, next);
});

router.put('/clientes/:cpf', (req, res, next) => {
    req.url = `/saga/alterarPerfil/${req.params.cpf}`;
    orquestradorServiceProxy(req, res, next);
});

router.post('/gerentes', (req, res, next) => {
    req.url = '/saga/inserirGerente';
    orquestradorServiceProxy(req, res, next);
});

router.delete('/gerentes/:cpf', (req, res, next) => {
    req.url = `/saga/removerGerente/${req.params.cpf}`;
    orquestradorServiceProxy(req, res, next);
});

router.post('/clientes/:cpf/rejeitar', clientesServiceProxy);
router.post('/clientes/:cpf/aprovar', clientesServiceProxy);

router.use('/gerentes', gerentesServiceProxy);
router.use('/clientes', clientesServiceProxy);
router.use('/contas', contasServiceProxy);

module.exports = router;