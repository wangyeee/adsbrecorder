export function feet2meters(feet) {
    return feet * 0.3048;
}

export function deg2rad(deg) {
    return deg * Math.PI / 180;
}

export function calculateDistance(lati0, long0, alti0, lati1, long1, alti1) {
    const r = 6.3781E6;
    var r0 = alti0 + r;
    var x0 = r0 * Math.cos(lati0) * Math.cos(long0);
    var y0 = r0 * Math.cos(lati0) * Math.sin(long0);
    var z0 = r0 * Math.sin(lati0);

    var r1 = alti1 + r;
    var x1 = r1 * Math.cos(lati1) * Math.cos(long1);
    var y1 = r1 * Math.cos(lati1) * Math.sin(long1);
    var z1 = r1 * Math.sin(lati1);

    return Math.sqrt(
        (x0 - x1) * (x0 - x1) +
        (y0 - y1) * (y0 - y1) +
        (z0 - z1) * (z0 - z1)
    );
}

export function displatKm(meters) {
    meters /= 1000;
    return meters.toFixed(3) + ' km';
}
