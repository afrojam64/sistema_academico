// ============================================
// CONFIGURACIÓN
// ============================================
const API_URL = 'http://localhost:8080';

// ============================================
// ELEMENTOS DEL DOM
// ============================================
const loginForm = document.getElementById('loginForm');
const btnLogin = document.getElementById('btnLogin');
const btnText = btnLogin.querySelector('.btn-text');
const btnSpinner = btnLogin.querySelector('.btn-spinner');
const nombreUsuarioInput = document.getElementById('nombreUsuario');
const contrasenaInput = document.getElementById('contrasena');
const togglePasswordBtn = document.getElementById('togglePassword');
const recordarmeCheckbox = document.getElementById('recordarme');
const forgotPasswordLink = document.getElementById('forgotPassword');

// ============================================
// TOGGLE MOSTRAR/OCULTAR CONTRASEÑA
// ============================================
togglePasswordBtn.addEventListener('click', function() {
    const type = contrasenaInput.type === 'password' ? 'text' : 'password';
    contrasenaInput.type = type;

    const icon = this.querySelector('i');
    icon.classList.toggle('fa-eye');
    icon.classList.toggle('fa-eye-slash');
});

// ============================================
// VALIDACIÓN EN TIEMPO REAL
// ============================================
nombreUsuarioInput.addEventListener('input', function() {
    validateField(this);
});

contrasenaInput.addEventListener('input', function() {
    validateField(this);
});

function validateField(field) {
    if (field.value.trim() === '') {
        field.classList.remove('is-valid');
        field.classList.add('is-invalid');
        return false;
    } else {
        field.classList.remove('is-invalid');
        field.classList.add('is-valid');
        return true;
    }
}

// ============================================
// RECORDAR USUARIO
// ============================================
// Cargar usuario guardado al iniciar
window.addEventListener('DOMContentLoaded', function() {
    const savedUsername = localStorage.getItem('savedUsername');
    if (savedUsername) {
        nombreUsuarioInput.value = savedUsername;
        recordarmeCheckbox.checked = true;
        validateField(nombreUsuarioInput);
    }
});

// ============================================
// MANEJO DEL FORMULARIO DE LOGIN
// ============================================
loginForm.addEventListener('submit', async function(e) {
    e.preventDefault();

    // Validar campos
    const isUsuarioValid = validateField(nombreUsuarioInput);
    const isContrasenaValid = validateField(contrasenaInput);

    if (!isUsuarioValid || !isContrasenaValid) {
        showError('Por favor, completa todos los campos');
        loginForm.classList.add('shake');
        setTimeout(() => loginForm.classList.remove('shake'), 500);
        return;
    }

    // Obtener datos del formulario
    const loginData = {
        nombreUsuario: nombreUsuarioInput.value.trim(),
        contrasena: contrasenaInput.value
    };

    // Mostrar spinner y deshabilitar botón
    setLoadingState(true);

    try {
        // Realizar petición al backend
        const response = await axios.post(`${API_URL}/auth/login`, loginData, {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        // Login exitoso
        handleLoginSuccess(response.data);

    } catch (error) {
        // Login fallido
        handleLoginError(error);
    } finally {
        // Ocultar spinner y habilitar botón
        setLoadingState(false);
    }
});

// ============================================
// FUNCIÓN: MANEJAR LOGIN EXITOSO
// ============================================
function handleLoginSuccess(data) {
    // Guardar token en localStorage
    localStorage.setItem('token', data.token);
    localStorage.setItem('tokenType', data.type);

    // Guardar información del usuario
    localStorage.setItem('userId', data.id);
    localStorage.setItem('userName', data.nombreUsuario);
    localStorage.setItem('userEmail', data.email);
    localStorage.setItem('userRole', data.rol);

    // Guardar usuario si está marcado "Recordarme"
    if (recordarmeCheckbox.checked) {
        localStorage.setItem('savedUsername', data.nombreUsuario);
    } else {
        localStorage.removeItem('savedUsername');
    }

    // Mostrar mensaje de éxito
    Swal.fire({
        icon: 'success',
        title: '¡Bienvenido!',
        text: `Hola ${data.nombreUsuario}`,
        timer: 1500,
        showConfirmButton: false,
        allowOutsideClick: false
    }).then(() => {
        // Redireccionar según el rol
        redirectByRole(data.rol);
    });
}

// ============================================
// FUNCIÓN: REDIRECCIONAR SEGÚN ROL
// ============================================
function redirectByRole(rol) {
    // Normalizar el rol (puede venir como "Super Administrador" o "SUPER_ADMIN")
    const rolNormalizado = rol.toUpperCase().replace(/\s+/g, '_');

    switch(rolNormalizado) {
        case 'SUPER_ADMINISTRADOR':
        case 'SUPER_ADMIN':
            window.location.href = '/dashboard/super-admin';
            break;
        case 'ADMINISTRADOR':
        case 'ADMIN':
            window.location.href = '/dashboard/admin';
            break;
        case 'PROFESOR':
            window.location.href = '/dashboard/profesor';
            break;
        case 'ESTUDIANTE':
            window.location.href = '/dashboard/estudiante';
            break;
        default:
            window.location.href = '/dashboard';
            break;
    }
}

// ============================================
// FUNCIÓN: MANEJAR ERROR DE LOGIN
// ============================================
function handleLoginError(error) {
    console.error('Error en login:', error);

    let errorMessage = 'Error al iniciar sesión';

    if (error.response) {
        // El servidor respondió con un código de error
        switch(error.response.status) {
            case 401:
                errorMessage = 'Usuario o contraseña incorrectos';
                break;
            case 400:
                errorMessage = 'Datos inválidos. Verifica los campos.';
                break;
            case 500:
                errorMessage = 'Error en el servidor. Intenta más tarde.';
                break;
            default:
                errorMessage = error.response.data.message || 'Error desconocido';
        }
    } else if (error.request) {
        // La petición se hizo pero no hubo respuesta
        errorMessage = 'No se pudo conectar con el servidor';
    }

    // Mostrar error
    showError(errorMessage);

    // Animar el formulario (shake)
    loginForm.classList.add('shake');
    setTimeout(() => loginForm.classList.remove('shake'), 500);
}

// ============================================
// FUNCIÓN: MOSTRAR LOADING STATE
// ============================================
function setLoadingState(isLoading) {
    if (isLoading) {
        btnLogin.disabled = true;
        btnText.textContent = 'Iniciando sesión...';
        btnSpinner.classList.remove('d-none');
    } else {
        btnLogin.disabled = false;
        btnText.textContent = 'Iniciar Sesión';
        btnSpinner.classList.add('d-none');
    }
}

// ============================================
// FUNCIÓN: MOSTRAR ERROR CON SWEETALERT2
// ============================================
function showError(message) {
    Swal.fire({
        icon: 'error',
        title: 'Error',
        text: message,
        confirmButtonText: 'Entendido',
        confirmButtonColor: '#1e3a8a'
    });
}

// ============================================
// FUNCIÓN: MOSTRAR INFORMACIÓN
// ============================================
function showInfo(message) {
    Swal.fire({
        icon: 'info',
        title: 'Información',
        text: message,
        confirmButtonText: 'Entendido',
        confirmButtonColor: '#1e3a8a'
    });
}

// ============================================
// LINK "OLVIDÉ MI CONTRASEÑA"
// ============================================
forgotPasswordLink.addEventListener('click', function(e) {
    e.preventDefault();

    Swal.fire({
        icon: 'info',
        title: 'Recuperar Contraseña',
        html: 'Contacta al administrador del sistema para restablecer tu contraseña.<br><br>Email: <strong>admin@sistema.com</strong>',
        confirmButtonText: 'Entendido',
        confirmButtonColor: '#1e3a8a'
    });
});

// ============================================
// VERIFICAR SI YA HAY SESIÓN ACTIVA
// ============================================
window.addEventListener('DOMContentLoaded', function() {
    const token = localStorage.getItem('token');
    const userRole = localStorage.getItem('userRole');

    if (token && userRole) {
        // Ya hay una sesión activa, redireccionar
        showInfo('Ya tienes una sesión activa').then(() => {
            redirectByRole(userRole);
        });
    }
});

// ============================================
// MANEJO DE TECLA ENTER
// ============================================
contrasenaInput.addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        loginForm.dispatchEvent(new Event('submit'));
    }
});

// ============================================
// PREVENIR MÚLTIPLES ENVÍOS
// ============================================
let isSubmitting = false;

loginForm.addEventListener('submit', function(e) {
    if (isSubmitting) {
        e.preventDefault();
        return;
    }
    isSubmitting = true;

    setTimeout(() => {
        isSubmitting = false;
    }, 2000);
});

// ============================================
// LIMPIAR MENSAJES DE ERROR AL ESCRIBIR
// ============================================
[nombreUsuarioInput, contrasenaInput].forEach(input => {
    input.addEventListener('focus', function() {
        this.classList.remove('is-invalid');
    });
});

// ============================================
// LOG DE DESARROLLO (SOLO PARA DEBUG)
// ============================================
console.log('Login.js cargado correctamente');
console.log('API URL:', API_URL);