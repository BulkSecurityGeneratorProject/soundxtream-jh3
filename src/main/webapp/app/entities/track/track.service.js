(function() {
    'use strict';
    angular
        .module('soundxtream3App')
        .factory('Track', Track);

    Track.$inject = ['$resource', 'DateUtils'];

    function Track ($resource, DateUtils) {
        var resourceUrl =  'api/tracks/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.date_upload = DateUtils.convertDateTimeFromServer(data.date_upload);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
