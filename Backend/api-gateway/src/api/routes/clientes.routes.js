const { Router } = require('express');
const verifyToken = require('../../middlewares/auth.middleware.js');

const router = Router();

const clientesServiceProxy = httpProxy('http://localhost:5001'); 

router.post('/:cpf/aprovar', verifyToken, (req, res, next) => {
    clientesServiceProxy(req,res,next);
})

router.post('/:cpf/rejeitar', verifyToken, (req, res, next) => {
    clientesServiceProxy(req,res,next);
})

router.put('/:cpf', verifyToken, (req, res, next) => {
    clientesServiceProxy(req,res,next);
});

router.get('/:cpf', verifyToken, (req, res, next) => {
    clientesServiceProxy(req,res,next);
});

router.post('/', (req, res, next) => {
    clientesServiceProxy(req,res,next);
});

router.get('/', verifyToken, (req, res, next) => {
    clientesServiceProxy(req,res,next);
});

module.exports = router;