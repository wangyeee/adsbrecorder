'use strict';

import React from 'react';
import ReactDOM from 'react-dom';

const client = require('./client');

class App extends React.Component {
    constructor(props) {
        super(props);
        this.state = {airlines: []};
    }

    componentDidMount() {
        client({method: 'GET', path: '/api/randair?n=15'}).done(response => {
            this.setState({airlines: response.entity});
        });
    }

    render() {
        return (
                <AirlineList airlines={this.state.airlines}/>
        );
    }
}

class AirlineList extends React.Component {
    render() {
        var airlines = this.props.airlines.map(
                airline => <Airline key={airline.airlineID} airline={airline}/>
        );
        return (
                <table>
                <thead>
                <tr>
                <th>Name</th>
                <th>Call Sign</th>
                <th>ICAO</th>
                <th>IATA</th>
                <th>Country</th>
                </tr>
                </thead>
                <tbody>
                {airlines}
                </tbody>
                </table>
        );
    }
}

class Airline extends React.Component {
    render() {
        return (
                <tr>
                <td>{this.props.airline.name}</td>
                <td>{this.props.airline.callSign}</td>
                <td>{this.props.airline.icao}</td>
                <td>{this.props.airline.iata}</td>
                <td>{this.props.airline.country}</td>
                </tr>
        );
    }
}

ReactDOM.render(
    <App />,
    document.getElementById('react')
)
