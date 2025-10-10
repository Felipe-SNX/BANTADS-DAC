const { Router } = require('express');
const verifyToken = require('../../middlewares/auth.middleware.js');

const router = Router();
const gerentesServiceProxy = httpProxy('http://localhost:5003');

router.delete('/:cpf', verifyToken, (req, res, next) => {
    gerentesServiceProxy(req,res,next);
});

router.put('/:cpf', verifyToken, (req, res, next) => {
    gerentesServiceProxy(req,res,next);
});

router.get('/:cpf', verifyToken, (req, res, next) => {
    gerentesServiceProxy(req,res,next);
});

router.post('/', verifyToken, (req, res, next) => {
    gerentesServiceProxy(req,res,next);
});

router.get('/', verifyToken, (req, res, next) => {
    gerentesServiceProxy(req,res,next);
});

module.exports = router;