function searchCtrl($scope, $http, $compile) {
    $('#search').click(function() {
        var hotelId = $('#hotels').val();
        var persons = $('#persons').val();
        var beginDate = $('#start').val();
        var endDate = $('#end').val();

        if (hotelId!='' && persons != '' && beginDate != '' && endDate != '') {
            $.ajax({
                type: "GET",
                url: "http://localhost:8080/rooms/search",
                dataType: "json",
                data: {
                    hotelId: hotelId,
                    persons: persons,
                    beginDate: beginDate,
                    endDate: endDate
                },
                success: function (response) {
                    $scope.rooms_available = response;
                    var html = "<table class=table>"+
                        "<tr>"+
                        "<th>Hotel</th>"+
                        "<th>Room Number</th>"+
                        "<th>Price</th>"+
                        "<th>Occupancy</th>"+
                        "<th></th>"+
                        "</tr>"+
                        "<tr ng-repeat='room in rooms_available'>"+
                        "<td>{{room.hotel.name}}</td>"+
                        "<td>{{room.roomNumber}}</td>"+
                        "<td>{{room.price}} €/night</td>"+
                        "<td>{{room.type.occupancy}}</td>"+

                        "<td><a ng-click='bookFunction(room.hotel.id)' class='btn btn-default'>Book a room</a></td>"+
                        "</tr>"+
                        "</table>";

                    var linkingFunction = $compile(html);
                    var elem = linkingFunction($scope);
                    $('#main').replace(elem);
                }
            });
        }
        else {
            alert("All fields need to be filled!");
        }

    });
    $scope.bookFunction = function(hotelId) {
        $http.get('http://localhost:8080/bookings/new/' + hotelId + '.json').
        success(function(data) {
            $scope.booking = data;
        });
    }
}