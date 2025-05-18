document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('form');
    const nameInput = document.getElementById('name');
    const phoneInput = document.getElementById('phone');
    const addressInput = document.getElementById('address'); // New address field

    // Validate Full Name
    nameInput.addEventListener('input', function () {
        const fullName = nameInput.value.trim();
        const nameRegex = /^[A-Za-z]+(?: [A-Za-z]+){1,2}$/; // 2 or 3 words, only letters allowed
        if (!nameRegex.test(fullName)) {
            nameInput.setCustomValidity('Full Name must be 2 or 3 words, only letters allowed.');
        } else {
            nameInput.setCustomValidity('');
        }
    });

    // Validate Phone Number
    phoneInput.addEventListener('input', function () {
        const phone = phoneInput.value.trim();
        const phoneRegex = /^(97|98)\d{8}$/; // Phone must start with 97 or 98 and be exactly 10 digits long
        if (!phoneRegex.test(phone)) {
            phoneInput.setCustomValidity('Phone must start with 97 or 98 and be exactly 10 digits long.');
        } else {
            phoneInput.setCustomValidity('');
        }
    });

    // Validate Address
    addressInput.addEventListener('input', function () {
        const address = addressInput.value.trim();
        const addressRegex = /^[A-Za-z]+\s\d+,\s[A-Za-z]+$/; // Matches format: local level ward no, district
        if (!addressRegex.test(address)) {
            addressInput.setCustomValidity(
                'Address should be in the format of local level ward no, district. Example: Dingla 2, Bhojpur.'
            );
        } else {
            addressInput.setCustomValidity('');
        }
    });

    // Form Submission
    form.addEventListener('submit', function (event) {
        // Trigger validation for all fields
        nameInput.dispatchEvent(new Event('input'));
        phoneInput.dispatchEvent(new Event('input'));
        addressInput.dispatchEvent(new Event('input'));

        // Prevent form submission if any validation fails
        if (!form.checkValidity()) {
            event.preventDefault(); // Prevent form submission if validation fails
        }
    });
});
