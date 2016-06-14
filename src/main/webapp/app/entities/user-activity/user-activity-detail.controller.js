(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .controller('User_activityDetailController', User_activityDetailController);

    User_activityDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'User_activity', 'User', 'Track', 'Playlist'];

    function User_activityDetailController($scope, $rootScope, $stateParams, entity, User_activity, User, Track, Playlist) {
        var vm = this;

        vm.user_activity = entity;

        var unsubscribe = $rootScope.$on('soundxtream3App:user_activityUpdate', function(event, result) {
            vm.user_activity = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
