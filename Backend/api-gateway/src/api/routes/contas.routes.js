const { Router } = require('express');
const verifyToken = require('../../middlewares/auth.middleware.js');
const { createProxyMiddleware } = require('http-proxy-middleware');

const router = Router();
const contasServiceProxy = createProxyMiddleware({
    target: process.env.MS_CONTA_URL,
    changeOrigin: true,
});

router.get('contas/:numero/saldo', (req, res, next) => {
    contasServiceProxy(req,res,next);
});

router.post('/:numero/depositar', verifyToken, (req, res, next) => {
    contasServiceProxy(req,res,next);
});

router.post('/:numero/sacar', verifyToken, (req, res, next) => {
    contasServiceProxy(req,res,next);
});

router.post('/:numero/transferir', verifyToken, (req, res, next) => {
    contasServiceProxy(req,res,next);
});

router.get('/:numero/extrato', verifyToken, (req, res, next) => {
    contasServiceProxy(req,res,next);
});

module.exports = router;