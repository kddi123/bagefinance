export function formatNumber(num: number) {
  return num.toFixed(2);
}

export function formatAmount(amount: number) {
  return `${amount}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')
};