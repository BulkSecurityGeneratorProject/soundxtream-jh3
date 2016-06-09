(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .controller('StyleDetailController', StyleDetailController);

    StyleDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'DataUtils', 'entity', 'Style'];

    function StyleDetailController($scope, $rootScope, $stateParams, DataUtils, entity, Style) {
        var vm = this;

        vm.style = entity;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('soundxtream3App:styleUpdate', function(event, result) {
            vm.style = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
