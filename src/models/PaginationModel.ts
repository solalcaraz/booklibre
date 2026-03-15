export interface PagReqModel <tData> {
  pageSize: number;
  pageNumber: number;
  filter?: tData;
}

export interface PagResModel <tData> {
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
