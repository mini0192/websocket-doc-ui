function openTab(tabName) {
    const tabContents = document.getElementsByClassName('tab-content');
    for (let i = 0; i < tabContents.length; i++) {
        tabContents[i].style.display = 'none';
    }

    const tabButtons = document.getElementsByClassName('tab-button');
    for (let i = 0; i < tabButtons.length; i++) {
        tabButtons[i].classList.remove('active');
    }

    document.getElementById(tabName).style.display = 'block';
    event.currentTarget.classList.add('active');

    if (tabName === 'documentation') {
        loadDocumentation();
    }
}

function loadDocumentation() {
    fetch('/v1/docs')
        .then(response => response.json())
        .then(data => {
            const tableBody = document.getElementById('api-table-body');
            // Clear existing content before adding new rows
            if (tableBody) {
                tableBody.innerHTML = '';
                data.forEach(item => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${item.topic}</td>
                        <td>${item.requestType}</td>
                        <td>${item.responseType}</td>
                        <td>${item.description}</td>
                    `;
                    tableBody.appendChild(row);
                });
            }
        })
        .catch(error => console.error('Error fetching API data:', error));
}

// Function to copy text to clipboard
function copyToClipboard(text) {
    navigator.clipboard.writeText(text).then(() => {
        console.log('Text copied to clipboard');
    }).catch(err => {
        console.error('Failed to copy text: ', err);
    });
}

document.addEventListener('DOMContentLoaded', function () {
    // Initially open the documentation tab
    document.querySelector('.tab-button.active').click();

    // Add event listeners for topic copy buttons
    document.querySelectorAll('.topic-copy-button').forEach(button => {
        button.addEventListener('click', function() {
            const topicText = this.closest('.topic').querySelector('.topic-text').textContent;
            copyToClipboard(topicText);
        });
    });

    // Add event listeners for payload copy buttons
    document.querySelectorAll('.copy-button').forEach(button => {
        button.addEventListener('click', function() {
            const payloadContent = this.closest('.payload-container').querySelector('.payload-content').textContent;
            copyToClipboard(payloadContent);
        });
    });
});