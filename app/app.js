document.addEventListener('DOMContentLoaded', () => {
    fetchExchangeRates();
});

function fetchExchangeRates() {
    fetch('http://localhost:8080/api/v1/currency-exchange-rates')
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch exchange rates. Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            displayExchangeRates(data.exchangeRateInfo);
        })
        .catch(error => {
            console.error('Error fetching exchange rates:', error);
        });
}

function displayExchangeRates(exchangeRates) {
    const tableBody = document.getElementById('exchangeRateTableBody');
    
    for (const rateInfo of exchangeRates) {
        const row = `<tr>
                        <td>${rateInfo.currency_code}</td>
                        <td>${rateInfo.currency_name}</td>
                        <td>${rateInfo.buying}</td>
                        <td>${rateInfo.tele_buying}</td>
                        <td>${rateInfo.selling}</td>
                        <td>${rateInfo.bank_name}</td>
                    </tr>`;
        tableBody.innerHTML += row;
    }
}
