 document.addEventListener('DOMContentLoaded', () => {
     const createCampaignForm = document.getElementById('create-campaign-form');
     const previewAudienceButton = document.getElementById('preview-audience-button');
     const audienceSizeDisplay = document.getElementById('audience-size-display');
     const campaignsTableBody = document.querySelector('#campaigns-table tbody');

     // Function to load and display campaigns
     async function loadCampaigns() {
         const campaigns = await window.api.fetchData('/campaigns');
         campaignsTableBody.innerHTML = ''; // Clear existing rows
         if (campaigns && campaigns.length > 0) {
             for (const campaign of campaigns) {
                 const row = campaignsTableBody.insertRow();
                 row.insertCell().textContent = campaign.id;
                 row.insertCell().textContent = campaign.name;
                 row.insertCell().textContent = campaign.segmentRules;
                 row.insertCell().textContent = campaign.audienceSize;
                 row.insertCell().textContent = campaign.sentDate ? new Date(campaign.sentDate).toLocaleString() : 'N/A';

                 // Fetch delivery stats for each campaign
                 const details = await window.api.fetchData(`/campaigns/${campaign.id}/details`);
                 if (details) {
                     const sentCell = row.insertCell();
                     sentCell.textContent = details.sentCount;
                     sentCell.classList.add('text-success'); // Apply green color

                     const failedCell = row.insertCell();
                     failedCell.textContent = details.failedCount;
                     failedCell.classList.add('text-danger'); // Apply red color

                     const statusCell = row.insertCell();
                     const totalProcessed = details.sentCount + details.failedCount;
                     if (totalProcessed === campaign.audienceSize) {
                         statusCell.textContent = 'Completed';
                         statusCell.classList.add('text-success');
                     } else if (totalProcessed > 0 && totalProcessed < campaign.audienceSize) {
                         statusCell.textContent = 'In Progress';
                         statusCell.style.color = '#ffc107'; // Yellow for in progress
                         statusCell.style.fontWeight = 'bold';
                     } else {
                         statusCell.textContent = 'Pending';
                         statusCell.style.color = '#6c757d'; // Grey for pending
                     }

                 } else {
                     row.insertCell().textContent = 'N/A';
                     row.insertCell().textContent = 'N/A';
                     row.insertCell().textContent = 'Error fetching stats';
                 }
             }
         } else {
             const row = campaignsTableBody.insertRow();
             const cell = row.insertCell();
             cell.colSpan = 8;
             cell.textContent = 'No campaigns found. Create one above!';
             cell.style.textAlign = 'center';
             cell.style.padding = '20px';
             cell.style.color = '#888';
         }
     }

     // Handle Preview Audience Size Button Click
     previewAudienceButton.addEventListener('click', async () => {
         const segmentRules = document.getElementById('segment-rules').value;
         if (!segmentRules) {
             audienceSizeDisplay.textContent = 'Please enter segmentation rules.';
             audienceSizeDisplay.style.color = 'orange';
             return;
         }

         const requestData = { segmentRules: segmentRules };
         const audienceSize = await window.api.postData('/campaigns/preview-audience', requestData);

         if (audienceSize !== null) {
             audienceSizeDisplay.textContent = `Estimated Audience Size: ${audienceSize} customers`;
             audienceSizeDisplay.style.color = audienceSize > 0 ? '#28a745' : '#dc3545';
         } else {
             audienceSizeDisplay.textContent = 'Failed to preview audience.';
             audienceSizeDisplay.style.color = '#dc3545';
         }
     });

     // Handle Create Campaign Form Submission
     createCampaignForm.addEventListener('submit', async (event) => {
         event.preventDefault();
         const campaignData = {
             name: document.getElementById('campaign-name').value,
             segmentRules: document.getElementById('segment-rules').value,
             messageTemplate: document.getElementById('message-template').value,
         };

         const newCampaign = await window.api.postData('/campaigns', campaignData);
         if (newCampaign) {
             alert('Campaign created and launched successfully!'); // Replace with custom modal
             createCampaignForm.reset();
             audienceSizeDisplay.textContent = ''; // Clear preview
             loadCampaigns(); // Reload campaign list
         }
     });

     // Load campaigns when the campaign section is activated
     document.querySelector('.nav-button[data-target="campaign-section"]').addEventListener('click', loadCampaigns);

     // Initial load if campaign section is the default active
     // loadCampaigns();
 });