'use strict';

import React from 'react';
import ReactDOM from 'react-dom';
import Time from 'react-time';

const client = require('./client');

class FlightHistoryListTable extends React.Component {
    constructor(props) {
        super(props);
        this.state = {dates: []};
    }

    componentDidMount() {
        client({method: 'GET', path: '/api/dates?p=0&n=10'}).done(response => {
            this.setState({dates: response.entity});
        });
    }

    render() {
        var dateList = this.state.dates.map(
            date => <FlightHistoryCell key={date} date={date}/>
        );
        return (
            <table className="table table-striped">
                <thead>
                    <tr>
                        <td scope="col">Date</td>
                        <td scope="col">View</td>
                    </tr>
                </thead>
                <tbody>
                    {dateList}
                </tbody>
            </table>
        );
    }
}

class FlightHistoryCell extends React.Component {
    constructor(props) {
        super(props);
        this.loadFlights = this.loadFlights.bind(this);
        this.state = {flightsLoaded : false};
    }

    loadFlights() {
        if (this.state.flightsLoaded) {
            ReactDOM.render(
                <p style={{display: 'none'}}/>,
                document.getElementById(this.props.date)
            );
            this.setState({ flightsLoaded : false });
        } else {
            ReactDOM.render(
                <FlightHistoryList date={this.props.date} />,
                document.getElementById(this.props.date)
            );
            this.setState({ flightsLoaded : true });
        }
    }

    render() {
        return (
            <tr>
                <td><Time value={new Date(this.props.date)} format="DD/MM/YYYY"/></td>
                <td>
                    <a href={'#'} onClick={this.loadFlights}>{'View'}</a>
                    <div id={this.props.date}></div>
                </td>
            </tr>
        );
    }
}

class FlightHistoryList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {flights: []};
    }

    componentDidMount() {
        client({method: 'GET', path: '/api/flights/' + this.props.date + '?p=0&n=10'}).done(response => {
            this.setState({flights: response.entity});
        });
    }

    render() {
        var flightList = this.state.flights.map(
            flight => <tr key={flight.flightID}>
                        <td><a href={'/map?f=' + flight.flightNumber + '&date=' + this.props.date} target={'_blank'} title={'View on map'}>{flight.flightNumber}</a></td>
                        <td>{flight.airline.name}</td>
                      </tr>
        );
        return (
                <table>
                    <tbody>
                        {flightList}
                    </tbody>
                </table>
        );
    }
}

ReactDOM.render(
    <FlightHistoryListTable />,
    document.getElementById('histbl')
);
