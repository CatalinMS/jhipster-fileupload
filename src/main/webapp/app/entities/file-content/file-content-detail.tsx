import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction, openFile, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './file-content.reducer';
import { IFileContent } from 'app/shared/model/file-content.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IFileContentDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class FileContentDetail extends React.Component<IFileContentDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { fileContentEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            FileContent [<b>{fileContentEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="name">Name</span>
            </dt>
            <dd>{fileContentEntity.name}</dd>
            <dt>
              <span id="content">Content</span>
            </dt>
            <dd>
              {fileContentEntity.content ? (
                <div>
                  <a onClick={openFile(fileContentEntity.contentContentType, fileContentEntity.content)}>Open&nbsp;</a>
                  <span>
                    {fileContentEntity.contentContentType}, {byteSize(fileContentEntity.content)}
                  </span>
                </div>
              ) : null}
            </dd>
          </dl>
          <Button tag={Link} to="/entity/file-content" replace color="info">
            <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/file-content/${fileContentEntity.id}/edit`} replace color="primary">
            <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
          </Button>
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = ({ fileContent }: IRootState) => ({
  fileContentEntity: fileContent.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(FileContentDetail);
