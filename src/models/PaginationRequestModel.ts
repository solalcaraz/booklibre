export interface PaginationRequestModel<tData> {
  pageSize: number;
  pageNumber: number;
  filter?: tData;
}
