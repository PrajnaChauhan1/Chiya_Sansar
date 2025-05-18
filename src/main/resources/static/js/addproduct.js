document.querySelector('.product-form').addEventListener('submit', function (e) {
  let formValid = true; // Flag to check form validity

 

  // Price validation
  const price = document.getElementById('price');
  const priceValue = parseFloat(price.value);
  if (isNaN(priceValue) || priceValue < 50 || priceValue > 5000) {
    price.setCustomValidity('Price must be a number between 50 and 5000.');
    formValid = false;
  } else {
    price.setCustomValidity('');
  }

 

  // Ingredients validation (optional)
  const ingredients = document.getElementById('ingredients');
  if (!ingredients.value.trim()) {
    ingredients.setCustomValidity('Ingredients cannot be empty.');
    formValid = false;
  } else {
    ingredients.setCustomValidity('');
  }

  // Stock validation
  const stock = document.getElementById('stock');
  const stockValue = parseInt(stock.value, 10);
  if (isNaN(stockValue) || stockValue <= 0) {
    stock.setCustomValidity('Stock must be a positive number.');
    formValid = false;
  } else {
    stock.setCustomValidity('');
  }

  // If any validation failed, prevent form submission
  if (!formValid) {
    e.preventDefault();
  }
});
