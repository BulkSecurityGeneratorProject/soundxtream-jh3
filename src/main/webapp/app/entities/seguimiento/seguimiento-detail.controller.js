(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .controller('SeguimientoDetailController', SeguimientoDetailController);

    SeguimientoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Seguimiento', 'User'];

    function SeguimientoDetailController($scope, $rootScope, $stateParams, entity, Seguimiento, User) {
        var vm = this;

        vm.seguimiento = entity;

        var unsubscribe = $rootScope.$on('soundxtream3App:seguimientoUpdate', function(event, result) {
            vm.seguimiento = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
