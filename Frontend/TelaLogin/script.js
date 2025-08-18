document.getElementById('loginForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value.trim();
    const statusMessage = document.getElementById('statusMessage');

    // Resetar estado
    statusMessage.textContent = '';
    statusMessage.className = 'alert mt-3 d-none';

    if (email === '' || password === '') {
        statusMessage.textContent = 'Por favor, preencha todos os campos.';
        statusMessage.className = 'alert alert-danger mt-3';
    } else {
        statusMessage.textContent = 'Login realizado com sucesso! Redirecionando...';
        statusMessage.className = 'alert alert-success mt-3';

        console.log('E-mail:', email);
        console.log('Senha:', password);
    }
});
