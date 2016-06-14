(function() {
    'use strict';
    angular
        .module('soundxtream3App')
        .factory('Seguimiento', Seguimiento);

    Seguimiento.$inject = ['$resource', 'DateUtils'];

    function Seguimiento ($resource, DateUtils) {
        var resourceUrl =  'api/seguimientos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.followingDate = DateUtils.convertDateTimeFromServer(data.followingDate);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
