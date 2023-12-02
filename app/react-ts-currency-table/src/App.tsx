// MainComponent.tsx
import React, { useState } from 'react';
import CurrencyTable from './components/CurrencyTable';
import DatePicker from './components/CustomDatePicker';
import Currency from './components/interfaces/Currency';
import './components/css/Custom.css'
import axios from 'axios';

const MainComponent: React.FC = () => {
  const [selectedDate, setSelectedDate] = useState<Date>(new Date());
  const [currencyData, setCurrencyData] = useState<Currency[]>([]);

  const fetchData = async (date: Date) => {
    try {
      const response = await axios.get(`/api/v1/currency-exchange-rates/${date.toISOString().split('T')[0]}`);
      setCurrencyData(response.data);
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  };

  const handleDateChange = (date: Date) => {
    setSelectedDate(date);
    fetchData(date);
  };

  return (
    <div className="container">
      <div className="header">
        <h1>Currency Exchange Rates</h1>
        <DatePicker selectedDate={selectedDate} onDateChange={handleDateChange} />
      </div>
      {currencyData.length > 0 ? (
        <div>
          <CurrencyTable data={currencyData} />
        </div>
      ) : (
        <p>No content</p>
      )}
    </div>
  );
};

export default MainComponent;
