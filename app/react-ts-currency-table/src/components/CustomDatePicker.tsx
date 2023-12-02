// CustomDatePicker.tsx
import React from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import styled from "styled-components";

interface DatePickerProps {
  selectedDate: Date;
  onDateChange: (date: Date) => void;
}

const StyledDatePicker = styled(DatePicker)`
  font-size: 16px;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  cursor: pointer;
`;

const CustomDatePicker: React.FC<DatePickerProps> = ({
  selectedDate,
  onDateChange,
}) => {
  const maxDate = new Date(); // Today's date

  return (
    <StyledDatePicker
      selected={selectedDate}
      onChange={(date: Date) => onDateChange(date)}
      dateFormat="dd/MM/yyyy"
      placeholderText="Select date"
      maxDate={maxDate} // Set the maximum allowed date
    />
  );
};

export default CustomDatePicker;
