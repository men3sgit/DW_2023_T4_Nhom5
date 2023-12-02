// CurrencyTable.tsx

import React from 'react';
import Currency from './interfaces/Currency';
import './css/CurrencyTable.css'; // Import the CSS file
interface CurrencyTableProps {
  data: Currency[];
}
const formatCurrency = (value: number | null) => {
  if (value === null) {
    return '-'; // or any default value you want for null
  }
  return value.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
};
const CurrencyTable: React.FC<CurrencyTableProps> = ({ data }) => {
  return (
    <table>
      <thead>
        <tr>
          <th>#</th>
          <th>Currency Code</th>
          <th>Currency Name</th>
          <th>Buying</th>
          <th>Telegraphic Buying</th>
          <th>Selling</th>
          <th>Bank Name</th>
          <th>Update At</th>
        </tr>
      </thead>
      <tbody>
        {data.map((currency, index) => (
          <tr key={index}>
            <td>{index + 1}</td>
            <td>{currency.currencyCode}</td>
            <td>{currency.currencyName}</td>
            <td>{formatCurrency(currency.cashBuying)}</td>
            <td>{formatCurrency(currency.telegraphicBuying)}</td>
            <td>{formatCurrency(currency.selling)}</td>
            <td>{currency.bankName}</td>
            <td>{currency.publishTime}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default CurrencyTable;
