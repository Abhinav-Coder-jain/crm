document.addEventListener('DOMContentLoaded', () => {
    const navButtons = document.querySelectorAll('.nav-button');
    const contentSections = document.querySelectorAll('.content-section');

    // Function to show/hide sections based on navigation
    function showSection(targetId) {
        contentSections.forEach(section => {
            if (section.id === targetId) {
                section.classList.add('active');
            } else {
                section.classList.remove('active');
            }
        });
    }

    // Add click listeners to navigation buttons
    navButtons.forEach(button => {
        button.addEventListener('click', () => {
            navButtons.forEach(btn => btn.classList.remove('active'));
            button.classList.add('active');
            showSection(button.dataset.target);
        });
    });

    // Initial load: show the dashboard section
    showSection('dashboard-section');

    // --- API Utility Functions (can be moved to a separate `api.js` if preferred) ---
    const API_BASE_URL = '/api'; // Spring Boot backend API base URL

    async function fetchData(endpoint) {
        try {
            const response = await fetch(`${API_BASE_URL}${endpoint}`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error('Error fetching data:', error);
            alert('Failed to fetch data. Check console for details.'); // Use alert for quick demo, replace with custom modal
            return null;
        }
    }

    async function postData(endpoint, data) {
        try {
            const response = await fetch(`${API_BASE_URL}${endpoint}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data),
            });
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
            }
            return await response.json();
        } catch (error) {
            console.error('Error posting data:', error);
            alert('Failed to save data: ' + error.message); // Use alert for quick demo, replace with custom modal
            return null;
        }
    }

    // --- Dashboard Stats Loading ---
    async function loadDashboardStats() {
        const totalCustomersElement = document.getElementById('total-customers');
        const totalCampaignsElement = document.getElementById('total-campaigns');

        const customers = await fetchData('/customers');
        if (customers) {
            totalCustomersElement.textContent = customers.length;
        } else {
            totalCustomersElement.textContent = 'N/A';
        }

        const campaigns = await fetchData('/campaigns');
        if (campaigns) {
            totalCampaignsElement.textContent = campaigns.length;
        } else {
            totalCampaignsElement.textContent = 'N/A';
        }
    }

    // Load dashboard stats when the dashboard section is activated
    // Or, for simplicity, load on DOMContentLoaded
    loadDashboardStats();

    // Re-load stats when navigating to dashboard
    document.querySelector('.nav-button[data-target="dashboard-section"]').addEventListener('click', loadDashboardStats);

    // Make utility functions globally available or pass them to other scripts
    window.api = {
        fetchData,
        postData
    };
});