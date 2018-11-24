'use strict';

import React from 'react';
import ReactDOM from 'react-dom';
import Time from 'react-time';
import GoogleMapReact from 'google-map-react';
import * as Geomath from './geomath.js';

const client = require('./client');

class AircraftDetails extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div>
                <div>
                    <p class="h5">{this.props.flight.flightNumber + ' (' + this.props.flight.airline.name + ')'}</p>
                </div>
                <table className="table table-striped">
                    <thead>
                        <tr>
                            <th scope="col">Altitude</th>
                            <th scope="col">Heading</th>
                            <th scope="col">Velocity</th>
                            <th scope="col">Time</th>
                            <th scope="col">Distance</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>{this.props.alti + ' feet'}</td>
                            <td>{this.props.hdrg + ' DEG'}</td>
                            <td>{this.props.velocity + ' knots'}</td>
                            <td><Time value={new Date(this.props.recordDate)} format="DD/MM/YYYY HH:mm:ss" /></td>
                            <td>{Geomath.displatKm(Geomath.calculateDistance(
                                Geomath.deg2rad(this.props.lati),
                                Geomath.deg2rad(this.props.long),
                                Geomath.feet2meters(this.props.alti),
                                Geomath.deg2rad(this.props.rloc.lati),
                                Geomath.deg2rad(this.props.rloc.long),
                                this.props.rloc.alti * 1.0))}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        );
    }
}

class Aircraft extends React.Component {
    constructor(props) {
        super(props);
        this.showDetails = this.showDetails.bind(this);
    }

    showDetails() {
        ReactDOM.render(
            <AircraftDetails alti={this.props.alti}
                hdrg={this.props.hdrg}
                lati={this.props.lat}
                long={this.props.lng}
                velocity={this.props.velocity}
                flight={this.props.flight}
                recordDate={this.props.recordDate}
                rloc={this.props.rloc} />,
            document.getElementById('details')
        );
    }

    render() {
        return (
            <img style={{ transform: 'rotate(' + this.props.hdrg + 'deg)' }}
                src="/plane.png"
                onClick={this.showDetails}
                height="32" width="32" />
        );
    }
}

class SimpleMap extends React.Component {
    constructor(props) {
        super(props);
        this.state = { flightNumber: document.getElementById('flightNumber').innerHTML,
                       flightDate : document.getElementById('flightDate').innerHTML,
                       gmapKey: "",
                       rloc: {} };
        this.fetchLiveData = this.fetchLiveData.bind(this);
        this.fetchReceiverLocation = this.fetchReceiverLocation.bind(this);
    }

    fetchLiveData() {
        client({ method: 'GET', path: '/api/gmap' }).done(response => {
            this.setState({ gmapKey: response.entity.key });
            client({ method: 'GET', path: '/api/' + this.state.flightNumber + '/' + this.state.flightDate }).done(response => {
                this.trackingRecords = response.entity.filter((record) =>
                    record.latitude != 0 && record.longitude != 0).map((record) =>
                        <Aircraft
                            key={record.recordID}
                            lat={record.latitude}
                            lng={record.longitude}
                            hdrg={record.heading}
                            alti={record.altitude}
                            recordDate={record.recordDate}
                            velocity={record.velocity}
                            flight={record.flight}
                            rloc={this.state.rloc} />
                    );
                this.forceUpdate();
            });
        });
    }

    fetchReceiverLocation() {
        client({method: 'GET', path: '/api/rloc'}).done(response => {
            this.setState({rloc: response.entity});
        });
    }

    componentDidMount() {
        this.fetchReceiverLocation();
        this.fetchLiveData();
        //setInterval(this.fetchLiveData, 1000);
    }

    render() {
        if (this.state.gmapKey != "") {
            return (
                <GoogleMapReact
                    bootstrapURLKeys={{ key: this.state.gmapKey }}
                    defaultCenter={this.props.center}
                    defaultZoom={this.props.zoom}>
                    {this.trackingRecords}
                </GoogleMapReact>
            );
        } else {
            return (<p>Loading maps...</p>);
        }
    }
}

ReactDOM.render(
    <div style={{ width: '100%', height: '100%' }}>
        <SimpleMap center={{ lat: parseFloat(document.getElementById('cenlati').innerHTML), lng: parseFloat(document.getElementById('cenlong').innerHTML) }} zoom={11} />
    </div>,
    document.getElementById('map')
);
