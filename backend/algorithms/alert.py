def calculate_alert(pm25, pm10, aqi):
    if pm25 is not None and pm25 > 75 or pm10 is not None and pm10 > 100 or aqi is not None and aqi > 150:
        return "High"
    if pm25 is not None and pm25 > 35 or pm10 is not None and pm10 > 50 or aqi is not None and aqi > 80:
        return "Moderate"
    return "Low"
