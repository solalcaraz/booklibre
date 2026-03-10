export interface PaginationResponseModel <tData> {
  data: tData[];
  totalItems: number;
  totalPages: number;
}
