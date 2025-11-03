const { Router } = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');

const { verifyToken, checkRole } = require('../../middlewares/auth.middleware');
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
    req.url = '/saga/autocadastro';
    orquestradorServiceProxy(req, res, next);
});

router.put('/clientes/:cpf', verifyToken, (req, res, next) => {
    req.url = `/saga/alterarPerfil/${req.params.cpf}`;
    orquestradorServiceProxy(req, res, next);
});

router.post('/gerentes', verifyToken, checkRole(['ADMIN']), (req, res, next) => {
    req.url = '/saga/inserirGerente';
    orquestradorServiceProxy(req, res, next);
});

router.delete('/gerentes/:cpf', verifyToken, checkRole(['ADMIN']), (req, res, next) => {
    req.url = `/saga/removerGerente/${req.params.cpf}`;
    orquestradorServiceProxy(req, res, next);
});

router.post('/clientes/:cpf/rejeitar', verifyToken, clientesServiceProxy);
router.post('/clientes/:cpf/aprovar', verifyToken, clientesServiceProxy);

router.use('/gerentes', verifyToken, gerentesServiceProxy);
router.use('/clientes', verifyToken, clientesServiceProxy);
router.use('/contas', verifyToken, contasServiceProxy);

module.exports = router;