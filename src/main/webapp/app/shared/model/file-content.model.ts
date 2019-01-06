export interface IFileContent {
  id?: number;
  name?: string;
  contentContentType?: string;
  content?: any;
}

export const defaultValue: Readonly<IFileContent> = {};
