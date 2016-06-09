(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .controller('StyleDialogController', StyleDialogController);

    StyleDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Style'];

    function StyleDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Style) {
        var vm = this;

        vm.style = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.style.id !== null) {
                Style.update(vm.style, onSaveSuccess, onSaveError);
            } else {
                Style.save(vm.style, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('soundxtream3App:styleUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setImage = function ($file, style) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        style.image = base64Data;
                        style.imageContentType = $file.type;
                    });
                });
            }
        };

    }
})();
