import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Row, Table } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { openFile, byteSize, ICrudGetAllAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntities } from './file-content.reducer';
import { IFileContent } from 'app/shared/model/file-content.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IFileContentProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export class FileContent extends React.Component<IFileContentProps> {
  componentDidMount() {
    this.props.getEntities();
  }

  render() {
    const { fileContentList, match } = this.props;
    return (
      <div>
        <h2 id="file-content-heading">
          File Contents
          <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create new File Content
          </Link>
        </h2>
        <div className="table-responsive">
          <Table responsive>
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Content</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {fileContentList.map((fileContent, i) => (
                <tr key={`entity-${i}`}>
                  <td>
                    <Button tag={Link} to={`${match.url}/${fileContent.id}`} color="link" size="sm">
                      {fileContent.id}
                    </Button>
                  </td>
                  <td>{fileContent.name}</td>
                  <td>
                    {fileContent.content ? (
                      <div>
                        <a onClick={openFile(fileContent.contentContentType, fileContent.content)}>Open &nbsp;</a>
                        <span>
                          {fileContent.contentContentType}, {byteSize(fileContent.content)}
                        </span>
                      </div>
                    ) : null}
                  </td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${fileContent.id}`} color="info" size="sm">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${fileContent.id}/edit`} color="primary" size="sm">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${fileContent.id}/delete`} color="danger" size="sm">
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        </div>
      </div>
    );
  }
}

const mapStateToProps = ({ fileContent }: IRootState) => ({
  fileContentList: fileContent.entities
});

const mapDispatchToProps = {
  getEntities
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(FileContent);
