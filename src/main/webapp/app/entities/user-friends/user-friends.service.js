(function() {
    'use strict';
    angular
        .module('soundxtream3App')
        .factory('User_friends', User_friends);

    User_friends.$inject = ['$resource', 'DateUtils'];

    function User_friends ($resource, DateUtils) {
        var resourceUrl =  'api/user-friends/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.friendshipDate = DateUtils.convertDateTimeFromServer(data.friendshipDate);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' },
            'friendshipNotifications': {
                method: 'GET',
                isArray: true,
                url: 'api/friendship_notifications'
            }
        });
    }
})();
