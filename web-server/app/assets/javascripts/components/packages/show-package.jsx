define(function(require) {

  var _ = require('underscore'),
      Router = require('react-router'),
      VehiclesForPackage = require('../vehicles/vehicles-for-package'),
      VehiclesQueuedForPackage = require('../vehicles/list-of-vehicles-queued-for-package'),
      PackageFilterAssociation = require('../package-filters/package-filter-association'),
      AffectedVins = require('../vehicles/affected-vins'),
      SotaDispatcher = require('sota-dispatcher'),
      db = require('stores/db'),
      React = require('react');

  var ShowPackageComponent = React.createClass({
    contextTypes: {
      router: React.PropTypes.func
    },
    componentWillUnmount: function(){
      this.props.Package.removeWatch("poll-package");
    },
    componentWillMount: function(){
      var params = this.context.router.getCurrentParams();
      SotaDispatcher.dispatch({
        actionType: 'get-package',
        name: params.name,
        version: params.version
      });
      this.props.Package.addWatch("poll-package", _.bind(this.forceUpdate, this, null));
    },
    render: function() {
      var params = this.context.router.getCurrentParams();
      var rows = _.map(this.props.Package.deref(), function(value, key) {
        return (
          <tr key={key}>
            <td>
              {key}
            </td>
            <td>
              {value}
            </td>
          </tr>
        );
      });
      return (
        <div>
          <h1>
            Packages &gt; {this.props.Package.deref().id}
          </h1>
          <div className="row">
            <div className="col-md-12">
              <Router.Link to='new-campaign' params={{name: params.name, version: params.version}}>
                <button className="btn btn-primary pull-right" name="new-campaign">
                  NEW CAMPAIGN
                </button>
              </Router.Link>
            </div>
          </div>
          <br/>
          <div className="row">
            <div className="col-md-12">
              <table className="table table-striped table-bordered">
                <thead>
                  <tr>
                    <td>
                      {params.name}
                    </td>
                    <td>
                    </td>
                  </tr>
                </thead>
                <tbody>
                  { rows }
                </tbody>
              </table>
            </div>
          </div>
          <PackageFilterAssociation
            Resource={this.props.Package}
            CreateList={db.filters}
            DeleteList={db.filtersForPackage}
            getCreateList="get-filters"
            createResourceName="Filters"
            getDeleteList={{actionType: 'get-filters-for-package', name: params.name, version: params.version}}/>
          <br/>
          <AffectedVins AffectedVins={db.affectedVins} />
          <h2>Vehicles</h2>
          <VehiclesForPackage VehiclesForPackage={db.vehiclesForPackage}/>
          <VehiclesQueuedForPackage Vehicles={db.vehiclesQueuedForPackage} PackageName={params.name} PackageVersion={params.version}/>
        </div>
      );
    }
  });

  return ShowPackageComponent;
});