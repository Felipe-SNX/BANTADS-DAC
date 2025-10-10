const { Router } = require('express');
const verifyToken = require('../../middlewares/auth.middleware.js');

const router = Router();
const contasServiceProxy = httpProxy('http://localhost:5002');

router.post('/:numero/saldo', verifyToken, (req, res, next) => {
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

router.post('/:numero/extrato', verifyToken, (req, res, next) => {
    contasServiceProxy(req,res,next); 
});

module.exports = router;