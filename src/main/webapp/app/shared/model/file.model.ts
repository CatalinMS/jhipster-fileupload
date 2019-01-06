export interface IFile {
  id?: number;
  name?: string;
  contentContentType?: string;
  content?: any;
}

export const defaultValue: Readonly<IFile> = {};
