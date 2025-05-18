document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('form');
    const nameInput = document.getElementById('name');
    const phoneInput = document.getElementById('phone');
    const passwordInput = document.getElementById('password');
    const password2Input = document.getElementById('password2');

    // Validate Full Name
    nameInput.addEventListener('input', function () {
    const fullName = nameInput.value.trim();
    const nameRegex = /^[A-Za-z]+(?: [A-Za-z]+){1,2}$/;
    if (!nameRegex.test(fullName)) {
        nameInput.setCustomValidity('Full Name must be 2 or 3 words, only letters allowed.');
    } else {
        nameInput.setCustomValidity('');
    }
});


    // Validate Phone Number
    phoneInput.addEventListener('input', function () {
        const phone = phoneInput.value.trim();
        const phoneRegex = /^(97|98)\d{8}$/;
        if (!phoneRegex.test(phone)) {
            phoneInput.setCustomValidity('Phone must start with 97 or 98 and be exactly 10 digits long.');
        } else {
            phoneInput.setCustomValidity('');
        }
    });

    // Validate Password
    passwordInput.addEventListener('input', function () {
        const password = passwordInput.value;
        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d]{8,16}$/;
        if (!passwordRegex.test(password)) {
            passwordInput.setCustomValidity('Password must be 8-16 characters long and include at least one uppercase letter, one lowercase letter, and one number.');
        } else {
            passwordInput.setCustomValidity('');
        }
    });

    // Validate Retyped Password
    password2Input.addEventListener('input', function () {
        const password = passwordInput.value;
        const password2 = password2Input.value;
        if (password !== password2) {
            password2Input.setCustomValidity('Passwords do not match.');
        } else {
            password2Input.setCustomValidity('');
        }
    });

   

    // Form Submission
    form.addEventListener('submit', function (event) {
        // Trigger validation for all fields
        nameInput.dispatchEvent(new Event('input'));
        phoneInput.dispatchEvent(new Event('input'));
        passwordInput.dispatchEvent(new Event('input'));
        password2Input.dispatchEvent(new Event('input'));
        addressInput.dispatchEvent(new Event('input'));

        if (!form.checkValidity()) {
            event.preventDefault(); // Prevent form submission if validation fails
        }
    });
});
