'use strict';

import Time from 'react-time';
import React from 'react';
import ReactDOM from 'react-dom';
import * as Geomath from './geomath.js'; 

const client = require('./client');

class App extends React.Component {
    constructor(props) {
        super(props);
        this.state = {aircrafts: [],
                      rloc: {}};
        this.fetchLiveData = this.fetchLiveData.bind(this);
        this.fetchReceiverLocation = this.fetchReceiverLocation.bind(this);
    }

    fetchLiveData() {
        client({method: 'GET', path: '/api/live'}).done(response => {
            if (response.entity.length == 0)
                document.title = 'Live Data';
            else
                document.title = 'Live Data (' + response.entity.length + ')';
            this.setState({aircrafts: response.entity});
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
        setInterval(this.fetchLiveData, 1000);
    }

    render() {
        return (
                <AircraftList aircrafts={this.state.aircrafts} rloc={this.state.rloc}/>
        );
    }
}

class AircraftList extends React.Component {
    render() {
        var aircrafts = this.props.aircrafts.map(
                aircraft => <Aircraft key={aircraft.recordID} aircraft={aircraft} rloc={this.props.rloc}/>
        );
        return (
                <table className="table table-striped">
                <thead>
                <tr>
                <th scope="col">Flight Number</th>
                <th scope="col">Airline</th>
                <th scope="col">Airline Country</th>
                <th scope="col">Latitude</th>
                <th scope="col">Longitude</th>
                <th scope="col">Altitude</th>
                <th scope="col">Velocity</th>
                <th scope="col">Heading</th>
                <th scope="col">Distance</th>
                <th scope="col">View Track</th>
                <th scope="col">Update on</th>
                </tr>
                </thead>
                <tbody>
                {aircrafts}
                </tbody>
                </table>
        );
    }
}

class Aircraft extends React.Component {
    render() {
        return (
            <tr>
                <td><a target="_blank" href={'https://flightaware.com/live/flight/' + this.props.aircraft.flight.flightNumber}
                    title={'Check ' + this.props.aircraft.flight.flightNumber + ' On FlightAware'}>{this.props.aircraft.flight.flightNumber}</a></td>
                <td>{this.props.aircraft.flight.airline.name}</td>
                <td>{this.props.aircraft.flight.airline.country}</td>
                <td>{this.props.aircraft.latitude}</td>
                <td>{this.props.aircraft.longitude}</td>
                <td>{(0.3048 * this.props.aircraft.altitude).toFixed(0) + ' m (' + this.props.aircraft.altitude + ' ft)'}</td>
                <td>{(1.852 * this.props.aircraft.velocity).toFixed(2) + ' km/h (' + this.props.aircraft.velocity + ' kt)'}</td>
                <td>{this.props.aircraft.heading}</td>
                <td>{Geomath.displatKm(Geomath.calculateDistance(
                    Geomath.deg2rad(this.props.aircraft.latitude),
                    Geomath.deg2rad(this.props.aircraft.longitude),
                    Geomath.feet2meters(this.props.aircraft.altitude),
                    Geomath.deg2rad(this.props.rloc.lati),
                    Geomath.deg2rad(this.props.rloc.long),
                    this.props.rloc.alti * 1.0))}</td>
                <td><a target="_blank" href={'/map?f=' + this.props.aircraft.flight.flightNumber + '&date=' + new Date().toISOString().slice(0, 10)}>View</a></td>
                <td><Time value={new Date(this.props.aircraft.recordDate)} format="DD/MM/YYYY HH:mm:ss" /></td>
            </tr>
        );
    }
}

ReactDOM.render(
    <App />,
    document.getElementById('react')
);
