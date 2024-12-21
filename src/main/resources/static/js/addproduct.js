document.querySelector('.product-form').addEventListener('submit', function (e) {
  let formValid = true; // Flag to check form validity

  // Product Name validation
  const productName = document.getElementById('product-name');
  const productNamePattern = /^[a-zA-Z0-9()_]+$/;
  if (!productNamePattern.test(productName.value)) {
    productName.setCustomValidity('Product Name can only contain letters, numbers, parentheses (), and underscores _.');
    formValid = false;
  } else {
    productName.setCustomValidity('');
  }

  // Description validation
  const description = document.getElementById('description');
  const descriptionPattern = /^[a-zA-Z0-9\s]+$/;
  const wordCount = description.value.trim().split(/\s+/).length;
  if (!descriptionPattern.test(description.value) || wordCount < 5) {
    description.setCustomValidity('Description must contain only letters, numbers, spaces, and must have at least 5 words.');
    formValid = false;
  } else {
    description.setCustomValidity('');
  }

  // Price validation
  const price = document.getElementById('price');
  const priceValue = parseFloat(price.value);
  if (isNaN(priceValue) || priceValue < 50 || priceValue > 5000) {
    price.setCustomValidity('Price must be a number between 50 and 5000.');
    formValid = false;
  } else {
    price.setCustomValidity('');
  }

  // Weight validation
  const weight = document.getElementById('weight');
  const weightPattern = /^\d+\s?(kg|gm)$/i;
  if (!weightPattern.test(weight.value)) {
    weight.setCustomValidity('Weight must have a unit after the number, like "10 kg" or "200 gm".');
    formValid = false;
  } else {
    weight.setCustomValidity('');
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
