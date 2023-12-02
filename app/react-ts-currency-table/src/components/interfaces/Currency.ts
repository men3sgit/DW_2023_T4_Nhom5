// Currency.interface.ts
interface Currency {
    id: number;
    currencyCode: string;
    currencyName: string;
    cashBuying: number | null;
    telegraphicBuying: number | null;
    selling: number;
    bankName: string;
    publishDate: string;
    publishTime: string;
  }
  
  export default Currency;
  