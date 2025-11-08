const jwt = require('jsonwebtoken');
const axios = require('axios');

const JWT_SECRET = process.env.JWT_SECRET;
const JWT_BEARER = 'Bearer ';
const msAuthUrl = process.env.MS_AUTH_URL;

const verifyToken = async (req, res, next) => {
    const authHeader = req.headers['authorization'];
    const token = authHeader.split(' ')[1];

    if (!authHeader || !authHeader.startsWith(JWT_BEARER)) {
        return res.status(401).json({message: 'Acesso negado. Nenhum token foi fornecido.'});
    }
    try {
        const decodedPayload = jwt.verify(token, JWT_SECRET);
        try {
            const validationUrl = `${msAuthUrl}/auth/checkBlacklist?token=${token}`;
            await axios.get(validationUrl);
            return res.status(401).json({message: 'Token revogado (logout).'})
        } catch (error){
            //Se o retorno é 404, o token não está na blacklist, logo é válido
            if (error.response && error.response.status === 404) {
            } else {
                console.error("[Gateway] Erro ao checar blacklist:", error.message);
                throw new Error("Erro no serviço de validação de token.");
            }
        }
        req.user = decodedPayload;
        next();
    } catch (error) {
        console.error("Erro de verificação de token:", error.message);
        return res.status(401).json({message: 'Token inválido ou expirado.'});
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
