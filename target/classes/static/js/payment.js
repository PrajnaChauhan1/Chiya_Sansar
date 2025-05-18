  // Get the input element
        var input = document.getElementById('regname');

        // Add event listener for input event
        input.addEventListener('input', function() {
            // Get the input value
            var inputValue = input.value;

            // Regular expression to match only letters and spaces
            var regex = /^[A-Za-z\s]+$/;

            // Test if the input matches the regular expression
            if (!regex.test(inputValue)) {
                // If input doesn't match, set custom validity message
                input.setCustomValidity('Only letters and spaces are allowed.');
            } else {
                // If input matches, clear any custom validity message
                input.setCustomValidity('');
            }
        });