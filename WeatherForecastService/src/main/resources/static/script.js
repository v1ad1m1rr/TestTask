let chart = null;

    async function getWeather() {
        const city = document.getElementById('city').value.trim();
        const error = document.getElementById('error');
        error.textContent = '';

        if (!city) {
            error.textContent = 'Please enter a city';
            return;
        }

        try {
            const response = await fetch('/weather?city=' + encodeURIComponent(city));
            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.error || 'Unknown error');
            }

            displayWeather(data, city);
        } catch (err) {
            error.textContent = 'Error: ' + err.message;
            if (chart) chart.destroy();
            document.getElementById('hourlyTable').innerHTML = '';
        }
    }

    function displayWeather(data, city) {
        const times = data.hourly.time.slice(0, 24);
        const temps = data.hourly.temperature_2m.slice(0, 24);

        const ctx = document.getElementById('weatherChart').getContext('2d');
        if (chart) chart.destroy();

        chart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: times.map(t => t.substring(11, 16)),
                datasets: [{
                    label: 'Temperature °C - ' + city,
                    data: temps,
                    borderColor: 'blue',
                    backgroundColor: 'rgba(0,0,255,0.1)',
                    fill: true,
                    tension: 0.4
                }]
            },
            options: {
                scales: {
                    y: {
                        title: {
                            display: true,
                            text: 'Temperature (°C)'
                        }
                    },
                    x: {
                        title: {
                            display: true,
                            text: 'Time'
                        }
                    }
                }
            }
        });

        let table = '<h3>24-Hour Forecast for ' + city + '</h3><table>';
        table += '<tr><th>Time</th><th>Temperature</th></tr>';
        for (let i = 0; i < 24; i++) {
            table += `<tr><td>${times[i].substring(11, 16)}</td><td>${temps[i]}°C</td></tr>`;
        }
        table += '</table>';
        document.getElementById('hourlyTable').innerHTML = table;
    }

    document.getElementById('city').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') getWeather();
    });