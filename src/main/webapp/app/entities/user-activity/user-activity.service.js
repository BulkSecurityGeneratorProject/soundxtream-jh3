(function() {
    'use strict';
    angular
        .module('soundxtream3App')
        .factory('User_activity', User_activity);

    User_activity.$inject = ['$resource', 'DateUtils'];

    function User_activity ($resource, DateUtils) {
        var resourceUrl =  'api/user-activities/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.dateLiked = DateUtils.convertDateTimeFromServer(data.dateLiked);
                        data.sharedDate = DateUtils.convertDateTimeFromServer(data.sharedDate);
                        data.uploadDate = DateUtils.convertDateTimeFromServer(data.uploadDate);
                        data.createdDate = DateUtils.convertDateTimeFromServer(data.createdDate);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
