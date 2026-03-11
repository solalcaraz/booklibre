export interface PaginationResponseModel <tData> {
  items: tData[];
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
}
