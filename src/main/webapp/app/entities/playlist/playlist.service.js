(function() {
    'use strict';
    angular
        .module('soundxtream3App')
        .factory('Playlist', Playlist);

    Playlist.$inject = ['$resource', 'DateUtils'];

    function Playlist ($resource, DateUtils) {
        var resourceUrl =  'api/playlists/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.dateCreated = DateUtils.convertDateTimeFromServer(data.dateCreated);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
