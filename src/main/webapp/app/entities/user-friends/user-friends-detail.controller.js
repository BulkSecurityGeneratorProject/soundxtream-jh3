(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .controller('User_friendsDetailController', User_friendsDetailController);

    User_friendsDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'User_friends', 'User'];

    function User_friendsDetailController($scope, $rootScope, $stateParams, entity, User_friends, User) {
        var vm = this;

        vm.user_friends = entity;

        var unsubscribe = $rootScope.$on('soundxtream3App:user_friendsUpdate', function(event, result) {
            vm.user_friends = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
