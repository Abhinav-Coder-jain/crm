document.addEventListener('DOMContentLoaded', () => {
    const addCustomerForm = document.getElementById('add-customer-form');
    const addOrderForm = document.getElementById('add-order-form');
    const customersTableBody = document.querySelector('#customers-table tbody');

    // Function to load and display customers
    async function loadCustomers() {
        const customers = await window.api.fetchData('/customers');
        customersTableBody.innerHTML = ''; // Clear existing rows
        if (customers && customers.length > 0) {
            customers.forEach(customer => {
                const row = customersTableBody.insertRow();
                row.insertCell().textContent = customer.id;
                row.insertCell().textContent = customer.name;
                row.insertCell().textContent = customer.email;
                row.insertCell().textContent = customer.phone;
                row.insertCell().textContent = customer.totalSpend ? customer.totalSpend.toFixed(2) : '0.00';
                row.insertCell().textContent = customer.visitCount || 0;
                row.insertCell().textContent = customer.lastVisitDate || 'N/A';
            });
        } else {
            const row = customersTableBody.insertRow();
            const cell = row.insertCell();
            cell.colSpan = 7;
            cell.textContent = 'No customers found. Add some above!';
            cell.style.textAlign = 'center';
            cell.style.padding = '20px';
            cell.style.color = '#888';
        }
    }

    // Handle Add Customer Form Submission
    addCustomerForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const customerData = {
            name: document.getElementById('customer-name').value,
            email: document.getElementById('customer-email').value,
            phone: document.getElementById('customer-phone').value,
            // totalSpend, lastVisitDate, visitCount will be initialized by backend if not provided
        };

        const newCustomer = await window.api.postData('/customers', customerData);
        if (newCustomer) {
            alert('Customer added successfully!'); // Replace with custom modal
            addCustomerForm.reset();
            loadCustomers(); // Reload customer list
        }
    });

    // Handle Add Order Form Submission
    addOrderForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const orderData = {
            customerId: parseInt(document.getElementById('order-customer-id').value),
            amount: parseFloat(document.getElementById('order-amount').value),
            itemDetails: document.getElementById('order-item-details').value,
            // orderDate will be initialized by backend if not provided
        };

        const newOrder = await window.api.postData('/orders', orderData);
        if (newOrder) {
            alert('Order added successfully! Customer spend/visits updated.'); // Replace with custom modal
            addOrderForm.reset();
            loadCustomers(); // Reload customer list to show updated spend/visits
        }
    });

    // Load customers when the customer section is activated
    document.querySelector('.nav-button[data-target="customer-section"]').addEventListener('click', loadCustomers);

    // Initial load if customer section is the default active (it's not, dashboard is)
    // loadCustomers();
});