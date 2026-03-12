export interface PaginationResponseModel <tData> {
  items: tData[];
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
}

export type PaginationState = {
  page: number
  totalPages: number
  size: number
  totalItems: number
}
