const jwt = require('jsonwebtoken');

const JWT_SECRET = process.env.JWT_SECRET;
const JWT_BEARER = 'Bearer ';

const verifyToken = (req, res, next) => {
    const authHeader = req.headers['authorization'];

    if (!authHeader || !authHeader.startsWith(JWT_BEARER)) {
        return res.status(401).json({ message: 'Acesso negado. Nenhum token foi fornecido.' });
    }

    const token = authHeader.split(' ')[1];

    try {
        const decodedPayload = jwt.verify(token, JWT_SECRET);
        req.user = decodedPayload;
        next(); 
    } catch (error) {
        console.error("Erro de verificação de token:", error.message);
        return res.status(401).json({ message: 'Token inválido ou expirado.' });
    }
};


const checkRole = (allowedRoles) => {
    
    return (req, res, next) => {
        if (!req.user || !req.user.role) {
            return res.status(403).json({ message: 'Acesso negado. Informação de permissão não encontrada no token.' });
        }

        const userRole = req.user.role; 

        if (allowedRoles.includes(userRole)) {
            next(); 
        } else {
            return res.status(403).json({ message: 'Acesso negado. Você não tem permissão para este recurso.' });
        }
    };
};

module.exports = {
    verifyToken,
    checkRole
};
