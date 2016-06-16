(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .controller('ChatDetailController', ChatDetailController);

    ChatDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Chat', 'User'];

    function ChatDetailController($scope, $rootScope, $stateParams, entity, Chat, User) {
        var vm = this;

        vm.chat = entity;

        var unsubscribe = $rootScope.$on('soundxtream3App:chatUpdate', function(event, result) {
            vm.chat = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
